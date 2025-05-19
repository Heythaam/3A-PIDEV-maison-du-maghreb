<?php

namespace App\Form;

use App\Entity\Collaboration;
use App\Form\DataTransformer\FileToStringTransformer;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Type;
use Symfony\Component\Validator\Constraints\NotNull;

class CollaborationType extends AbstractType
{
    private FileToStringTransformer $fileTransformer;

    public function __construct(FileToStringTransformer $fileTransformer)
    {
        $this->fileTransformer = $fileTransformer;
    }
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('name', TextType::class,[
                'label' => 'Nom de collaboration',
                'required' => false,
                'attr' => ['class' => 'form-control formControlMedium d-block w-100'],
                'empty_data' => ''
            ])
            ->add('image', FileType::class, [
                'label' => false,
                'mapped' => false,
                'required' => true,
                'constraints' => [
                    new Assert\NotBlank([
                        'message' => 'Please select an image'
                    ]),
                    new Assert\File([
                        'maxSize' => '5M',
                        'mimeTypes' => [
                            'image/jpeg',
                            'image/png',
                            'image/gif'
                        ],
                        'mimeTypesMessage' => 'Please upload a valid image (JPEG, PNG, GIF)',
                        'maxSizeMessage' => 'The image file is too large. Maximum allowed size is 5MB.'
                    ])
                ],
                'attr' => [
                    'accept' => 'image/*',
                    'class' => 'image-upload-input'
                ]
            ])
            ->add('description', TextareaType::class, [
                'label' => 'Description',
                'required' => false,
                'attr' => ['class' => 'form-control formControlMedium d-block w-100', 'rows' => 5],
                'empty_data' => ''
            ])
            ->add('startDate', DateType::class, [
                'label' => 'Date de début',
                'widget' => 'single_text',
                'attr' => ['class' => 'form-control formControlMedium d-block w-100'],
                'required' => false,
                'empty_data' => null,
                'constraints' => [
                    new NotNull(['message' => 'La date de début est obligatoire.']),
                ],
            ])
            ->add('endDate', DateType::class, [
                'label' => 'Date de fin',
                'widget' => 'single_text',
                'attr' => ['class' => 'form-control formControlMedium d-block w-100'],
                'required' => false,
                'empty_data' => null,  
                'constraints' => [
                    new NotNull(['message' => 'La date de fin est obligatoire.']),
                ],
            ])
            ->add('type', ChoiceType::class, [
                'label' => 'Type de collaboration',
                'required' => false,
                'choices' => [
                    'Création d\'un artisanat' => 'Creation d\'un artisanat',
                    'Workshop' => 'Workshop',
                    'Organisation d\'évènement' => 'Organisation d\'evenement'
                ],
                'attr' => ['class' => 'form-control formControlMedium d-block w-100'],
                'empty_data' => ''
            ])
            ->add('budget', IntegerType::class, [
                'label' => 'Budget',
                'required' => true,
                'constraints' => [
                    new NotBlank(['message' => 'Le budget est obligatoire.']),
                    new Type(['type' => 'integer', 'message' => 'Le budget doit être un nombre.']),
                ],
                'attr' => [
                    'class' => 'form-control',
                    'placeholder' => 'Entrez le budget',
                ],
                'empty_data' => ' ',
            ])
            ->add('location', TextType::class, [
                'label' => 'Adresse',
                'required' => false,
                'attr' => ['class' => 'form-control formControlMedium d-block w-100'],
                'empty_data' => ''
            ])
            ->add('prerequis', TextType::class, [
                'label' => 'Prérequis',
                'required' => false,
                'attr' => ['class' => 'form-control formControlMedium d-block w-100'],
                'empty_data' => ''
            ])
            ->add('submit', SubmitType::class, [
                'label' => 'Créer la collaboration',
                'attr' => ['class' => 'd-block w-100 btn btn-secondary -uppercase'],
            ]);
        ;
        $builder->get('image')->addModelTransformer($this->fileTransformer);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Collaboration::class,
        ]);
    }
}
