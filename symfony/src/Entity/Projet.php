<?php

namespace App\Entity;

use App\Repository\ProjetRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Validator\Context\ExecutionContextInterface;

#[ORM\Entity(repositoryClass: ProjetRepository::class)]
class Projet
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Title cannot be blank")]
    #[Assert\Length(
        min: 3,
        max: 50,
        minMessage: "Title must be at least {{ limit }} characters long",
        maxMessage: "Title cannot be longer than {{ limit }} characters"
    )]
    private ?string $title = null;

    #[ORM\Column(type: Types::TEXT)]
    #[Assert\NotBlank(message: "Description cannot be blank")]
    #[Assert\Length(
        min: 10,
        max: 1000,
        minMessage: "Description must be at least {{ limit }} characters long",
        maxMessage: "Description cannot be longer than {{ limit }} characters"
    )]
    private ?string $description = null;

    #[ORM\Column(type: Types::DATE_MUTABLE, nullable: true)]
    #[Assert\NotBlank(message: "La date est requise.")]
    private ?\DateTimeInterface $startDate = null;

    #[ORM\Column(type: Types::DATE_MUTABLE, nullable: true)]
    #[Assert\NotBlank(message: "La date est requise.")]
    #[Assert\Expression(
        "this.getStartDate() < this.getEndDate()",
        message: "End date must be after start date"
    )]
    private ?\DateTimeInterface $endDate = null;

    #[ORM\Column(length: 255)]
    private ?string $status = null;

    #[ORM\Column(type: Types::DECIMAL, precision: 10, scale: 2)]
    #[Assert\NotBlank(message: "Budget cannot be blank")]
    #[Assert\Positive(message: "Budget must be a positive number")]
    private ?string $budget = null;

    #[ORM\ManyToOne(inversedBy: 'projets')]
    #[ORM\JoinColumn(nullable: false)]
    private ?Collaboration $collaboration = null;

    #[ORM\Column(length: 255, nullable: true)]
    #[Assert\File(
        maxSize: "5M",
        mimeTypes: ["image/jpeg", "image/png", "image/gif"],
        mimeTypesMessage: "Please upload a valid image (JPEG, PNG, GIF)",
        maxSizeMessage: "The image file is too large ({{ size }} {{ suffix }}). Maximum allowed size is {{ limit }} {{ suffix }}."
    )]
    private ?string $image = null;

    #[Assert\Callback]
    public function validateDateFin(ExecutionContextInterface $context): void
    {
        if ($this->startDate && $this->endDate) {
            if ($this->endDate < $this->startDate) {
                $context->buildViolation('La date de fin doit être après la date de début.')
                    ->atPath('endDate')
                    ->addViolation();
            }

            if ($this->collaboration) {
                $collaborationStartDate = $this->collaboration->getStartDate();
                $collaborationEndDate = $this->collaboration->getEndDate();

                if ($this->startDate < $collaborationStartDate) {
                    $context->buildViolation('La date de début doit être après la date de début de la collaboration.')
                        ->atPath('startDate')
                        ->addViolation();
                }

                if ($this->endDate > $collaborationEndDate) {
                    $context->buildViolation('La date de fin doit être avant la date de fin de la collaboration.')
                        ->atPath('endDate')
                        ->addViolation();
                }
            }
        }
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getTitle(): ?string
    {
        return $this->title;
    }

    public function setTitle(string $title): static
    {
        $this->title = $title;

        return $this;
    }

    public function getDescription(): ?string
    {
        return $this->description;
    }

    public function setDescription(string $description): static
    {
        $this->description = $description;

        return $this;
    }

    public function getStartDate(): ?\DateTimeInterface
    {
        return $this->startDate;
    }

    public function setStartDate(\DateTimeInterface $startDate): static
    {
        $this->startDate = $startDate;

        return $this;
    }

    public function getEndDate(): ?\DateTimeInterface
    {
        return $this->endDate;
    }

    public function setEndDate(\DateTimeInterface $endDate): static
    {
        $this->endDate = $endDate;

        return $this;
    }

    public function getStatus(): ?string
    {
        return $this->status;
    }

    public function setStatus(string $status): static
    {
        $this->status = $status;

        return $this;
    }

    public function getBudget(): ?string
    {
        return $this->budget;
    }

    public function setBudget(string $budget): static
    {
        $this->budget = $budget;

        return $this;
    }

    public function getCollaboration(): ?Collaboration
    {
        return $this->collaboration;
    }

    public function setCollaboration(?Collaboration $collaboration): static
    {
        $this->collaboration = $collaboration;

        return $this;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(?string $image): static
    {
        $this->image = $image;
        return $this;
    }
}
