<?php

namespace App\Form;

use App\Entity\Collaboration;
use App\Entity\Projet;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\NumberType;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Validator\Constraints\NotNull;

class ProjetType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
        ->add('title', TextType::class, [
            'label' => 'Title',
            'attr' => ['class' => 'form-control']
        ])
        ->add('description', TextareaType::class, [
            'label' => 'Description',
            'attr' => ['class' => 'form-control', 'rows' => 3]
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
        ->add('budget', NumberType::class, [
            'label' => 'Budget',
            'attr' => ['class' => 'form-control']
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
        ->add('submit', SubmitType::class, [
                'label' => 'Créer le projet',
                'attr' => ['class' => 'd-block w-100 btn btn-secondary -uppercase']
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Projet::class,
        ]);
    }
}
