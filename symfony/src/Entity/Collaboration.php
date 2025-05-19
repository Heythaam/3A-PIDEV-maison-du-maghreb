<?php

namespace App\Entity;

use App\Repository\CollaborationRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: CollaborationRepository::class)]
class Collaboration
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le nom de la collaboration est obligatoire.")]
    #[Assert\Length(
        min: 3,
        max: 50,
        minMessage: "Le nom doit contenir au moins {{ limit }} caractères.",
        maxMessage: "Le nom ne peut pas dépasser {{ limit }} caractères."
    )]
    private ?string $name = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Veuillez fournir une description.")]
    #[Assert\Length(
        min: 10,
        max: 1000,
        minMessage: "La description doit contenir au moins {{ limit }} caractères.",
        maxMessage: "La description ne peut pas dépasser {{ limit }} caractères."
    )]
    private ?string $description = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    #[Assert\NotBlank(message: "La date est requise.")]
    private ?\DateTimeInterface $startDate = null;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    #[Assert\NotBlank(message: "La date est requise.")]
    #[Assert\Expression(
        "this.getStartDate() < this.getEndDate()",
        message: "La date de fin doit être après la date de début."
    )]
    private ?\DateTimeInterface $endDate = null;

   
    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Le type de collaboration est requis.")]
    #[Assert\Choice(
        choices: ["Creation d'un artisanat", "Workshop", "Organisation d'evenement"],
        message: "Veuillez sélectionner un type valide."
    )]
    private ?string $type = null;

    #[ORM\Column(length: 255)]
    private ?string $status = null;

    #[ORM\Column]
    #[Assert\NotBlank(message: "Le budget est obligatoire.")]
    #[Assert\Positive(message: "Le budget doit être un nombre positif.")]
    private ?int $budget = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "L'adresse est requise.")]
    #[Assert\Length(
        min: 5,
        max: 255,
        minMessage: "L'adresse doit contenir au moins {{ limit }} caractères.",
        maxMessage: "L'adresse ne peut pas dépasser {{ limit }} caractères."
    )]
    private ?string $location = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: "Les prérequis sont requis.")]
    #[Assert\Length(
        min: 5,
        max: 255,
        minMessage: "Les prérequis doivent contenir au moins {{ limit }} caractères.",
        maxMessage: "Les prérequis ne peuvent pas dépasser {{ limit }} caractères."
    )]
    private ?string $prerequis = null;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $image = "";

    #[ORM\Column(type: 'float', nullable: true)]
    private ?float $budgetDeviation = null;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $qrCodePath = null;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $barcodePath = null;



    public function validateDateFin(ExecutionContextInterface $context): void
    {
        if ($this->startDate && $this->endDate) {
            if ($this->endDate < $this->startDate) {
                $context->buildViolation('La date de fin doit être après la date de début.')
                    ->atPath('dateFin')
                    ->addViolation();
            }
        }
    }
    /**
     * @var Collection<int, Projet>
     */
    #[ORM\OneToMany(mappedBy: "collaboration", targetEntity: Projet::class, cascade: ["remove"])]
    private Collection $projets;

    public function __construct()
    {
        $this->projets = new ArrayCollection();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getName(): ?string
    {
        return $this->name;
    }

    public function setName(string $name): static
    {
        $this->name = $name;

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

    public function getType(): ?string
    {
        return $this->type;
    }

    public function setType(string $type): static
    {
        $this->type=$type;

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

    public function getBudget(): ?int
    {
        return $this->budget;
    }

    public function setBudget(int $budget): static
    {
        $this->budget = $budget;

        return $this;
    }

    public function getLocation(): ?string
    {
        return $this->location;
    }

    public function setLocation(string $location): static
    {
        $this->location = $location;

        return $this;
    }

    public function getPrerequis(): ?string
    {
        return $this->prerequis;
    }

    public function setPrerequis(string $prerequis): static
    {
        $this->prerequis = $prerequis;

        return $this;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(string $image): static
    {
        $this->image = $image;

        return $this;
    }

    /**
     * @return Collection<int, Projet>
     */
    public function getProjets(): Collection
    {
        return $this->projets;
    }

    public function addProjet(Projet $projet): static
    {
        if (!$this->projets->contains($projet)) {
            $this->projets->add($projet);
            $projet->setCollaboration($this);
        }

        return $this;
    }

    public function removeProjet(Projet $projet): static
    {
        if ($this->projets->removeElement($projet)) {
            // set the owning side to null (unless already changed)
            if ($projet->getCollaboration() === $this) {
                $projet->setCollaboration(null);
            }
        }

        return $this;
    }
    public function getBudgetDeviation(): ?float
    {
        return $this->budgetDeviation;
    }

    public function setBudgetDeviation(?float $budgetDeviation): self
    {
        $this->budgetDeviation = $budgetDeviation;

        return $this;
    }
    public function getBarcodePath(): ?string
    {
        return $this->barcodePath;
    }

    public function setBarcodePath(?string $barcodePath): static
    {
        $this->barcodePath = $barcodePath;

        return $this;
    }

    public function getQrCodePath(): ?string
    {
        return $this->qrCodePath;
    }

    public function setQrCodePath(?string $qrCodePath): static
    {
        $this->qrCodePath = $qrCodePath;

        return $this;
    }
    public function generateQrCode(): void
    {
        $qrCode = new QrCode($this->getId()); // Generate QR code from the collaboration ID
        $qrCode->writeFile(__DIR__ . '/../../public/uploads/qr_codes/collaboration_' . $this->getId() . '.png');
        $this->setQrCodePath('/uploads/qr_codes/collaboration_' . $this->getId() . '.png');
    }

    /**
     * Generate the barcode for the collaboration.
     */
    public function generateBarcode(): void
    {
        $barcode = new BarcodeGenerator();
        $barcode->setText($this->getId()); // Generate barcode from the collaboration ID
        $barcode->setType(BarcodeGenerator::BARCODE_CODE128);
        $barcode->render(__DIR__ . '/../../public/uploads/barcodes/collaboration_' . $this->getId() . '.png');
        $this->setBarcodePath('/uploads/barcodes/collaboration_' . $this->getId() . '.png');
    }
}
